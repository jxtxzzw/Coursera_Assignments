score = input("Enter Score: ")
score = float(score)
if score > 1:
    print("error")
elif score >= 0.9:
    print("A")
elif score >= 0.8:
    print("B")
elif score >= 0.7:
    print("C")
elif score >= 0.6:
    print("D")
elif score >= 0: # score < 0.6
    print("F")
else:
    print("error")