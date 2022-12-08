#include <stdio.h>
#include <stdlib.h>

#define ENABLE_BIT_MASK 0x80
#define INITIAL_MASK 0x1
#define MASK_MAX_VALUE 0x80
#define SPEED_MASK 0x7


int main() {
    int mask = INITIAL_MASK;

    while (1) {
        if (roulette_enabled(ENABLE_BIT_MASK) == 0) {
            continue;
        }

        // ligar os leds
        ativar_leds(mask);

         // ativo
        delay(get_speed(SPEED_MASK));

        // shiftar maskara para proximo loop
        if (mask == MASK_MAX_VALUE) {
            mask = INITIAL_MASK;
        } else {
            mask = mask << 1;
        }
    
        delay(get_speed(SPEED_MASK));
    }
}

void ativar_leds(int mask) {
    outport_write();
}