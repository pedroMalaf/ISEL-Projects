#include <stdio.h>
#include <stdlib.h>
#include <assert.h>
#include <string.h>

int count_ones(int val)
{
    int count = 0;
    int tmp = val;
    while (tmp != 0)
    {
        if (tmp & 1)
        {
            count++;
        }
        tmp = tmp >> 1;
    }
    return count;
}

int count_zeros(int val)
{
    int count = 0;
    int tmp = val;
    for (int i = 0; i < sizeof(val) * 8; i++)
    {
        if ((tmp & 1) == 0)
        {
            count++;
        }
        tmp = tmp >> 1;
    }
    return count;
}

void print_bits(int val)
{
    int tmp = val;
    for (int i = 0; i < sizeof(val) * 8; i++)
    {
        int bit = tmp & 1;
        printf("%d\t", bit);
        tmp = tmp >> 1;
    }
    printf("\n");
}

#define ASCII_TABLE_SIZE 128

char most_frequent_symbols(char *file_name)
{
    int array[ASCII_TABLE_SIZE];
    for (int i = 0; i < ASCII_TABLE_SIZE; i++)
    {
        array[i] = 0;
    }

    FILE *fp = fopen(file_name, "r");
    char ch;
    while ((ch = fgetc(fp)) != EOF)
    {
        int ch_to_int = ch;
        array[ch]++;
    }

    int highest_val = array[0];
    for (int i = 1; i < ASCII_TABLE_SIZE; i++)
    {
        if (highest_val < array[i])
        {
            highest_val = i;
        }
    }

    return highest_val;
}

#define MAX_OUTPUT 128

void negative_file(char *input_file_name, char *output_file_name)
{
    char output[MAX_OUTPUT];
    for (int i = 0; i < MAX_OUTPUT; i++)
    {
        output[i] = 0;
    }

    FILE *input_file = fopen(input_file_name, "r");
    FILE *output_file = fopen(output_file_name, "w");
    char ch;
    while ((ch = fgetc(input_file)) != EOF)
    {
        fprintf(output_file, "%c", ~ch);
    }
    fclose(input_file);
    fclose(output_file);
}

int main()
{
    assert(count_ones(2) == 1);
    assert(count_ones(3) == 2);
    assert(count_ones(0xFFFF) == 16);

    assert(count_zeros(2) == 31);
    assert(count_zeros(3) == 30);

    printf("O simbolo mais repetido é: %c\n", most_frequent_symbols("asdf.txt"));
    printf("testes passaram\n");

    negative_file("asdf.txt", "test.txt");
}