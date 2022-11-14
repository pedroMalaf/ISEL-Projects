
; TODO: fazer aqui, testar, e depois sim passar po main
    .equ RAND_MAX, 0x7FFF

; uint16_t get_remainder(uint64_t num)
; num = (r3:r2:r1:r0)
; return value stored in r1:r0 (?)
get_remainder:
    push lr 
    push r4 
    mov r4, #RAND_MAX & 0xff
    movt r4, #(RAND_MAX >> 8)
while_loop:
    cmp r1, #0
    bne subtraction
    cmp r2, #0
    bne subtraction 
    cmp r3, #0 
    bne subtraction
    cmp r1, r4 
    blo return
subtraction:
    sub 

return:

    