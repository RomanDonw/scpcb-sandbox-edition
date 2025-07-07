import random

w = 256
h = 256
frames_count = 1

f = open("test.vidraw", "w")
for i in range(w * h * frames_count):
    f.write(chr(random.randint(0, 256)))
    f.write(chr(random.randint(0, 256)))
    f.write(chr(random.randint(0, 256)))


f.close()
