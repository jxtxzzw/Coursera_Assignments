fname = input("Enter file name: ")
fh = open(fname)
lst = list()
for line in fh:
    s = line.rstrip().split()
    for ss in s:
	    lst.append(ss)
lst = list(set(lst))
lst.sort()
print(lst)
    