name = input("Enter file:")
if len(name) < 1 : name = "mbox-short.txt"
handle = open(name)
dict = {}
cache = None
for line in handle:
    if line.startswith("From:"):
        email = line.split()[1]
        dict[email] = dict.get(email, 0) + 1
        if cache is None or dict[email] > dict[cache]:
            cache = email
print(cache, dict.get(cache))