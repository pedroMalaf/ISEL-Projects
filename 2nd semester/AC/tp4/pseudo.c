#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>
#include <stdbool.h>

#define SEV_SEG_OFF 0

// LER: PODE NÃO ESTAR COMPLETO DEVIDO
// ÀS ALTERAÇÕES FEITAS AO LONGO DO TRABALHO

uint8_t sm_state; // state machine state
bool MODE;
uint8_t BET;               // valor da aposta
uint8_t RANDOM_DICE_VALUE; // valor pseudo aleatorio (dado)

int main()
{
    // ptc_init(SYSCLK_FREQ);
    // setup cpsr

state_machine:
    switch (sm_state)
    {
    case 0:
        wait_for_mode();
        break;
    case 1:
        get_bet();
        break;
    case 2:
        wait_for_roll();
        break;
    case 3:
        sev_seg_roll_effect();
        break;
    case 4:
        show_random_dice_value();
        break;
    case 5:
        check_win();
        break;
    default:
        goto state_machine;
    }
}

// estado 0: resetta cenas, aguarda pelo I0 a ficar a 1, para iniciar (1. e 2.)
void wait_for_mode()
{
    outport_init(SEV_SEG_OFF);
    MODE = 0;
    RANDOM_DICE_VALUE = 0;
    BET = 0;

    while (1)
    {
        if (inport_read() & 0x1) // mode a 1
        {
            break;
        }
    }
    MODE = 1;
    sm_state = 1;
}

// estado 1: espera pela manipulação do I2..5 e mostra no segmento (valor da aposta) (3. e 4.)
void get_bet()
{
    while (1)
    {
        uint8_t value = (inport_read() & 0b00111100) >> 2;
        show_hex_sevseg(value, true);
        if ((inport_read() & 0x1) == 0) // mode (i0) a 0
        {
            break;
        }
    }
    MODE = 0;
    sm_state = 2;
}

// estado 2: apaga 7seg, espera pelo I7 (roll) (5. e 6.)
void wait_for_roll()
{
    outport_write(SEV_SEG_OFF);
    while (1)
    {
        uint8_t last_roll_value = (inport_read() & 0b10000000) >> 7;
        uint8_t current_roll_value = (inport_read() & 0b10000000) >> 7;
        if (last_roll_value == 0 && current_roll_value == 1) // transicao ascendente
        {
            break;
        }
    }
    sm_state = 3;
}

// estado 3: efeito luminoso de a. a f. de 200ms (7.)
void sev_seg_roll_effect()
{
    uint8_t array[] = {1, 2, 4, 8, 16, 64, 128}; // cada posição é o efeito (a,b,c,d) individualmente
    for (int j = 0; j < 5; j++)
    {
        for (int i = 0; i < 6; i++)
        {
            auto time = get_ticks();
            outport_write(array[i]);

            while (elapsed(time) < 200 /*ms*/)
            {
                continue;
            }
        }
    }
    sm_state = 4;
}

// estado 4: mostrar valor random do dado (8.)
void show_random_dice_value()
{
    RANDOM_DICE_VALUE = random();
    outport_write(RANDOM_DICE_VALUE);
    sleep(10); // 10 segundos
    sm_state = 5;
}

// estado 5: caso o valor random seja igual ao q apostamos (9.)
void check_win()
{
    if (BET == RANDOM_DICE_VALUE)
    {
        // o valor afixado no mostrador deve piscar ao ritmo de 800 ms com fator de ciclo de 75%
    }
    sleep(10);
    sm_state = 0;
}