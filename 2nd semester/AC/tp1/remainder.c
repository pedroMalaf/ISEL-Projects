// 1 possivel funcao exemplo para obter resto

int get_remainder(int num, int divisor)
{
    while (num >= divisor)
        num = num - divisor;
 
    return num;
}