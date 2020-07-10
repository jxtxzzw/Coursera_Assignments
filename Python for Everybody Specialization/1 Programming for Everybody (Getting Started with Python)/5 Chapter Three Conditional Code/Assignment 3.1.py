hrs = input("Enter Hours:")
h = float(hrs)
rate = input("Enter Rates:")
r = float(rate)
if h > 40:
    sum = (40 + (h - 40) * 1.5) * r
else:
    sum = h * r
print(sum)