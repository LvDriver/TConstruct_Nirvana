import struct, zlib

def read_png(path):
    with open(path, 'rb') as f:
        data = f.read()
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
    return w, h, bitdepth, colortype, zlib.decompress(idat), palette

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
        if f == 1:
            for i in range(bpp, stride):
                line[i] = (line[i] + line[i-bpp]) & 0xFF
        elif f == 2:
            for i in range(stride):
                line[i] = (line[i] + prev[i]) & 0xFF
        elif f == 3:
            for i in range(stride):
                a = line[i-bpp] if i >= bpp else 0
                line[i] = (line[i] + ((a + prev[i]) >> 1)) & 0xFF
        elif f == 4:
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

def main():
    w, h, bd, ct, raw, pal = read_png('src/main/resources/assets/tconstruct_nirvana/textures/gui/smeltery.png')
    bpp = 4
    data = unfilter(w, h, bpp, raw)
    # ASCII 可视化 (6,10) 到 (76,90)：主背景罐区域
    x0, y0, x1, y1 = 4, 10, 80, 92
    print('smeltery.png tank area (%d,%d)-(%d,%d):' % (x0, y0, x1, y1))
    for y in range(y0, y1):
        row = ''
        for x in range(x0, x1):
            off = (y*w + x) * 4
            r, g, b, a = data[off], data[off+1], data[off+2], data[off+3]
            if a < 40:
                row += '.'
            else:
                lum = (r + g + b) / 3
                row += '#' if lum > 128 else '+'
        print('%3d %s' % (y, row))
    # scala 区域 (176,76) 可视化
    x0, y0 = 176, 76
    print('scala (176,76,52x52):')
    for y in range(y0, y0 + 52):
        row = ''
        for x in range(x0, x0 + 52):
            off = (y*w + x) * 4
            r, g, b, a = data[off], data[off+1], data[off+2], data[off+3]
            if a < 40:
                row += '.'
            else:
                row += 'B' if b > 100 and r < 100 else '+'
        print(row)

if __name__ == '__main__':
    main()
