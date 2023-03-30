import matplotlib.pyplot as plt


def n_first_terms(n, u, r):
    term = u
    for i in range(1, n):
        print("o termo", i, "desta sucessão geométrica é:", term)
        term = term * r


def mdc(a, b):
    ax = a
    bx = b
    while ax != bx:
        if (ax > bx):
            ax = ax - bx
        else:
            bx = bx - ax
    mdc = ax
    return mdc


def most_frequent_symbols(input):
    char_count = {}
    with open(input, 'r') as file:
        contents = file.read()
        for char in contents:
            if char in char_count:
                char_count[char] += 1
            else:
                char_count[char] = 1

    most_frequent = max(char_count, key=char_count.get)
    least_frequent = min(char_count, key=char_count.get)

    print("The most frequent symbol is:", most_frequent, "appearing", char_count[most_frequent], "times",
          "and the least frequent symbol is:", least_frequent, "appearing", char_count[least_frequent], "times")


def info_of_file(input):
    char_count = {}
    with open(input, 'r') as file:
        contents = file.read()
        for char in contents:
            if char in char_count:
                char_count[char] += 1
            else:
                char_count[char] = 1

    key_list = list(char_count.keys())
    value_list = list(char_count.values())

    plt.bar(key_list, value_list)

    plt.xlabel('Symbol')
    plt.ylabel('nr of occurence')
    plt.title('Symbols histogram')

    plt.show()
