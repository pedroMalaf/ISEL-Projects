int count_ones(int val)
{
    int count = 0;
    int nr_in_binary = int_to_binary(val);
    int i;
    for (i = 0; i < sizeof(nr_in_binary); i++)
    {
        if (nr_in_binary & (1 << i))
        {
            count++;
        }
    }
    return count;
}

int count_zeroes(int val)
{
    int count = 0;
    int nr_in_binary = int_to_binary(val);
    int i;
    for (i = 0; i < sizeof(nr_in_binary); i++)
    {
    }
    return count
}

char *int_to_binary(int num)
{
    int index = 0;
    char *binary = (char *)malloc(sizeof(char) * 33); // Assuming 32-bit integers
    if (binary == NULL)
    {
        printf("Error: Memory allocation failed.\n");
        exit(1);
    }

    while (num > 0)
    {
        binary[index++] = (num % 2) + '0';
        num /= 2;
    }

    binary[index] = '\0';

    // Reverse the binary representation
    int i, j;
    char temp;
    for (i = 0, j = index - 1; i < j; i++, j--)
    {
        temp = binary[i];
        binary[i] = binary[j];
        binary[j] = temp;
    }

    return binary;
}