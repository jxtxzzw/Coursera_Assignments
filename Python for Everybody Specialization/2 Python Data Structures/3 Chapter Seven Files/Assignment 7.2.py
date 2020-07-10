# Use the file name mbox-short.txt as the file name
fname = input("Enter file name: ")
fh = open(fname)
s = 0
c = 0
for line in fh:
    if not line.startswith("X-DSPAM-Confidence:") : continue
    s += float(line[line.find(":") + 1:])
    c += 1
print("Average spam confidence:", (s / c))
