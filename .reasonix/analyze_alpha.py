import struct, zlib, sys

def read_png(path):
    with open(path, 'rb') as f:
        data = f.read()
    assert data[:8] == b'\x89PNG\r\n\x1a\n'
    pos = 8
    w = h = None
    bitdepth = colortype = None
    idat = b''
    palette = None
    while pos < len(data):
        ln = struct.unpack('>I', data[pos:pos+4])[0]
        typ = data[pos+4:pos+8]
        chunk = data[pos+8:pos+8+ln]
        if typ == b'IHDR':
            w, h, bitdepth, colortype = struct.unpack('>IIBB', chunk[:10])
        elif typ == b'PLTE':
            palette = chunk
        elif typ == b'IDAT':
            idat += chunk
        pos += 12 + ln
    raw = zlib.decompress(idat)
    return w, h, bitdepth, colortype, raw, palette

def unfilter(w, h, bpp, raw):
    stride = w * bpp
    out = bytearray()
    prev = bytearray(stride)
    pos = 0
    for y in range(h):
        f = raw[pos]
        pos += 1
        line = bytearray(raw[pos:pos+stride])
        pos += stride
        if f == 0:
            pass
        elif f == 1:  # Sub
            for i in range(bpp, stride):
                line[i] = (line[i] + line[i-bpp]) & 0xFF
        elif f == 2:  # Up
            for i in range(stride):
                line[i] = (line[i] + prev[i]) & 0xFF
        elif f == 3:  # Average
            for i in range(stride):
                a = line[i-bpp] if i >= bpp else 0
                line[i] = (line[i] + ((a + prev[i]) >> 1)) & 0xFF
        elif f == 4:  # Paeth
            for i in range(stride):
                a = line[i-bpp] if i >= bpp else 0
                b = prev[i]
                c = prev[i-bpp] if i >= bpp else 0
                p = a + b - c
                pa, pb, pc = abs(p-a), abs(p-b), abs(p-c)
                pr = a if (pa <= pb and pa <= pc) else (b if pb <= pc else c)
                line[i] = (line[i] + pr) & 0xFF
        out += line
        prev = line
    return bytes(out)

def to_rgba(w, h, bitdepth, colortype, raw, palette):
    bpp = {6: 4, 2: 3, 3: 1, 0: 1}[colortype]
    data = unfilter(w, h, bpp, raw)
    out = bytearray(w*h*4)
    for y in range(h):
        for x in range(w):
            off = (y*w + x) * bpp
            o = (y*w + x) * 4
            if colortype == 6:
                out[o:o+4] = data[off:off+4]
            elif colortype == 2:
                out[o:o+3] = data[off:off+3]
                out[o+3] = 255
            elif colortype == 0:
                v = data[off]
                out[o:o+4] = bytes((v, v, v, 255))
            elif colortype == 3:
                idx = data[off]
                p = palette[idx*3:idx*3+3]
                out[o:o+3] = p
                out[o+3] = 255
    return bytes(out)

def alpha_stats(rgba, w, h, x0, y0, x1, y1, label):
    a = {0: 0, 255: 0, 'other': 0}
    rgb_nonzero = 0
    for y in range(y0, y1):
        for x in range(x0, x1):
            o = (y*w + x) * 4
            r, g, b, al = rgba[o], rgba[o+1], rgba[o+2], rgba[o+3]
            if al == 0:
                a[0] += 1
                if (r, g, b) != (0, 0, 0):
                    rgb_nonzero += 1
            elif al == 255:
                a[255] += 1
            else:
                a['other'] += 1
    print(label, 'alpha0=%d alpha255=%d other=%d | rgb-nonzero-with-alpha0=%d' % (a[0], a[255], a['other'], rgb_nonzero))

def region_colors(rgba, w, h, x0, y0, x1, y1, label, topn=8):
    cols = {}
    for y in range(y0, y1):
        for x in range(x0, x1):
            o = (y*w + x) * 4
            c = tuple(rgba[o:o+4])
            if c[3] == 0:
                continue
            cols[c] = cols.get(c, 0) + 1
    top = sorted(cols.items(), key=lambda kv: -kv[1])[:topn]
    print(label, top)

def main():
    base = 'src/main/resources/assets/tconstruct_nirvana/textures/gui/'
    for name in ['smeltery.png', 'toolstation.png', 'tool_station.png']:
        w, h, bd, ct, raw, pal = read_png(base + name)
        rgba = to_rgba(w, h, bd, ct, raw, pal)
        print('==', name, w, 'x', h, 'colortype', ct)
        if name == 'smeltery.png':
            alpha_stats(rgba, w, h, 0, 0, 176, 166, 'main-bg')
            alpha_stats(rgba, w, h, 176, 76, 228, 128, 'scala')
            alpha_stats(rgba, w, h, 0, 166, 22, 184, 'slot')
            alpha_stats(rgba, w, h, 176, 150, 188, 166, 'progress-bars')
            alpha_stats(rgba, w, h, 0, 0, 256, 256, 'whole')
            region_colors(rgba, w, h, 176, 150, 188, 166, 'bar colors:')
            region_colors(rgba, w, h, 176, 76, 228, 128, 'scala colors:')
            region_colors(rgba, w, h, 0, 166, 22, 184, 'slot colors:')
            region_colors(rgba, w, h, 0, 0, 176, 166, 'main-bg colors:')
        if name == 'toolstation.png':
            alpha_stats(rgba, w, h, 0, 0, 176, 174, 'main-bg')
            alpha_stats(rgba, w, h, 176, 0, 212, 18, 'slots')
            region_colors(rgba, w, h, 176, 0, 212, 18, 'slot colors:')
            region_colors(rgba, w, h, 0, 0, 176, 174, 'main-bg colors:')

if __name__ == '__main__':
    main()
