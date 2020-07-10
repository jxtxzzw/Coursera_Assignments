def computepay(h,r):
    return h * r if h <= 40 else (40 + 1.5 * (h - 40)) * r

h = float(input("Enter Hours:"))
r = float(input("Enter Rate:"))
p = computepay(h,r)
print("Pay",p)