import struct

for f in ['panel.png', 'icons.png']:
    with open('src/main/resources/assets/tconstruct_nirvana/textures/gui/' + f, 'rb') as fp:
        d = fp.read(33)
    w, h = struct.unpack('>II', d[16:24])
    print(f, w, h)
