text = "X-DSPAM-Confidence:    0.8475";
ipos = text.find(":")
num = float(text[ipos + 1:])
print(num)