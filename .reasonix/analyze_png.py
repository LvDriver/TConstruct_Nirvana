import struct, zlib, sys

def read_png(path):
    with open(path, 'rb') as f:
        data = f.read()
    assert data[:8] == b'\x89PNG\r\n\x1a\n'
    pos = 8
    w = h = None
    idat = b''
    while pos < len(data):
        ln = struct.unpack('>I', data[pos:pos+4])[0]
        typ = data[pos+4:pos+8]
        chunk = data[pos+8:pos+8+ln]
        if typ == b'IHDR':
            w, h, bitdepth, colortype = struct.unpack('>IIBB', chunk[:10])
        elif typ == b'IDAT':
            idat += chunk
        pos += 12 + ln
    raw = zlib.decompress(idat)
    return w, h, bitdepth, colortype, raw

def pixel(raw, w, h, bitdepth, colortype, x, y):
    # only supports 8-bit RGB/RGBA/indexed(3)/grayscale
    if colortype == 6:
        bpp = 4
    elif colortype == 2:
        bpp = 3
    elif colortype == 3:
        bpp = 1
    elif colortype == 0:
        bpp = 1
    else:
        raise Exception('colortype %d unsupported' % colortype)
    stride = w * bpp + 1
    off = y * stride + 1 + x * bpp
    if colortype == 6:
        return tuple(raw[off:off+4])
    if colortype == 2:
        return tuple(raw[off:off+3])
    if colortype == 0:
        v = raw[off]
        return (v, v, v, 255)
    # indexed: need PLTE; fallback
    return (raw[off],)

def region_colors(raw, w, h, bitdepth, colortype, x0, y0, x1, y1):
    cols = {}
    for y in range(y0, y1):
        for x in range(x0, x1):
            c = pixel(raw, w, h, bitdepth, colortype, x, y)
            cols[c] = cols.get(c, 0) + 1
    return cols

def main():
    base = 'src/main/resources/assets/tconstruct_nirvana/textures/gui/'
    for name in ['smeltery.png', 'toolstation.png', 'tool_station.png']:
        try:
            w, h, bitdepth, colortype, raw = read_png(base + name)
            print('==', name, 'size', w, 'x', h, 'colortype', colortype)
            if name == 'smeltery.png':
                for label, (x0, y0, x1, y1) in {
                    'scala(176,76,52x52)': (176, 76, 228, 128),
                    'slot(0,166,22x18)': (0, 166, 22, 184),
                    'slotEmpty(22,166,22x18)': (22, 166, 44, 184),
                    'progress(176,150,3x16)': (176, 150, 179, 166),
                    'unprogress(179,150,3x16)': (179, 150, 182, 166),
                    'uber(182,150,3x16)': (182, 150, 185, 166),
                    'noMelt(185,150,3x16)': (185, 150, 188, 166),
                }.items():
                    cols = region_colors(raw, w, h, bitdepth, colortype, x0, y0, x1, y1)
                    # 显示主要颜色（出现次数 top5）
                    top = sorted(cols.items(), key=lambda kv: -kv[1])[:5]
                    print('  ', label, top)
            if name == 'toolstation.png':
                for label, (x0, y0, x1, y1) in {
                    'SlotBackground(176,0,18x18)': (176, 0, 194, 18),
                    'SlotBorder(194,0,18x18)': (194, 0, 212, 18),
                    'TextFieldActive(0,210,102x12)': (0, 210, 102, 222),
                    'ItemCover(176,18,80x64)': (176, 18, 256, 82),
                    'Arrow(0,241,8x15)': (0, 241, 16, 256),
                }.items():
                    cols = region_colors(raw, w, h, bitdepth, colortype, x0, y0, x1, y1)
                    top = sorted(cols.items(), key=lambda kv: -kv[1])[:5]
                    print('  ', label, top)
        except Exception as e:
            print('ERR', name, e)

if __name__ == '__main__':
    main()
