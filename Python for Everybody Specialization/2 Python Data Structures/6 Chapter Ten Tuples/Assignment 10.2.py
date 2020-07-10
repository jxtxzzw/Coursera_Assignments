name = input("Enter file:")
if len(name) < 1 : name = "mbox-short.txt"
handle = open(name)
d = {}
for line in handle:
    if line.startswith("From "):
        t = line.split()[5].split(":")[0]
        d[t] = d.get(t, 0) + 1
newt = []
for k, v in d.items():
    newt.append((k, v))
newt.sort()
for (k, v) in newt:
    print(k, v)