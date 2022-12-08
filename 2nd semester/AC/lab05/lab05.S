; Definicao dos valores dos simbolos utilizados no programa
;
	.equ	CPSR_BIT_I, 0b010000          ; Mascara para o bit I do registo CPSR

	.equ	STACK_SIZE, 64                ; Dimensao do stack - 64 B

	; Definicoes do porto de entrada
	.equ	INPORT_ADDRESS, 0xFF00        ; Endereco do porto de entrada

	; Definicoes do porto de saida
	.equ	OUTPORT_ADDRESS, 0xFF00       ; Endereco do porto de saida

	.equ	OUTPORT_INIT_VAL, 0           ; Valor inicial do porto de saida

	; Definicoes do circuito pTC
	.equ	PTC_ADDRESS,  0xff40          ; Endereco do circuito pTC

	.equ	PTC_TCR, 0                    ; Deslocamento do registo TCR do pTC
	.equ	PTC_TMR, 2                    ; Deslocamento do registo TMR do pTC
	.equ	PTC_TC, 4                     ; Deslocamento do registo TC do pTC
	.equ	PTC_TIR, 6                    ; Deslocamento do registo TIR do pTC

	.equ	PTC_CMD_START, 0              ; Comando para iniciar a contagem no pTC
	.equ	PTC_CMD_STOP, 1               ; Comando para parar a contagem no pTC

	.equ	SYSCLK_FREQ, 0x63             ; Intervalo de contagem do circuito pTC
                                          ; que suporta a implementação do sysclk

	.equ ENABLE_BIT_MASK, 0x80		; bit para começar a roletta
	.equ INITIAL_MASK, 0x1			; bit inicial da mascara da roletta
	.equ MASK_MAX_VALUE, 0x80		; bit maximo para a roletta
	.equ SPEED_MASK, 0x7			; mascara de bits que definem a velocidade

; Seccao:    .startup
; Descricao: Guarda o código de arranque do sistema
;
	.section .startup
	b	_start
	ldr	pc, isr_addr
_start:
	ldr	sp, tos_addr
	ldr	pc, main_addr

tos_addr:
	.word	tos
main_addr:
	.word	main
isr_addr:
	.word	isr

; Seccao:    .text
; Descricao: Guarda o código do programa
;
	.text

; Rotina:    main
; Descricao: System clock
; Entradas:  -
; Saidas:    -
; Efeitos:   r0, r1
main:
	mov	r0, #OUTPORT_INIT_VAL
	bl	outport_init
	mov	r0, #SYSCLK_FREQ
	bl	ptc_init
	mrs	r0, cpsr
	mov	r1, #CPSR_BIT_I
	orr	r0, r0, r1
	msr	cpsr, r0

	; meter mask a initial mask
	ldr r0, roulette_mask_addr
	mov r1, #INITIAL_MASK
	strb r1, [r0, #0]

reset_and_loop:
	mov r0, #OUTPORT_INIT_VAL
	bl outport_write

main_loop:
	bl inport_read
	mov r1, #ENABLE_BIT_MASK
	mov r2, #0
	and r0, r0, r1
	cmp r0, r2 	; if (enable_bit == 0)
	beq reset_and_loop ; continue

	ldr r0, roulette_mask_addr
	ldrb r0, [r0, #0]
	bl outport_write ; ativa os bits da mascara

	bl inport_read
	mov r1, #SPEED_MASK
	and r0, r0, r1 ; r0 = r0 & 0b111
	bl multiply ; r0 = speed
	;bl multiply ; "FITA COLA"
	;bl multiply ; "FITA COLA"
	bl delay ; dormir speed ms

	; shiftar mascara
	ldr r0, roulette_mask_addr
	ldrb r0, [r0, #0]
	mov r3, #MASK_MAX_VALUE
	cmp r0, r3
	bne if_not_equal
if_equal:
	mov r0, #INITIAL_MASK
	b shift_mask_end
if_not_equal:
	lsl r0, r0, #1
shift_mask_end:
	ldr r1, roulette_mask_addr
	strb r0, [r1, #0]

	bl inport_read
	mov r1, #SPEED_MASK
	and r0, r0, r1
	bl multiply ; r0 = speed
	;bl multiply ; "FITA COLA"
	;bl multiply ; "FITA COLA"
	bl delay ; dormir speed ms

	b main_loop


roulette_mask_addr:
	.word roulette_mask

; Rotina:    multiply
; Descricao: Multiplica o valor de r0
; Entradas:  r0 = n
; Saidas:    r0 = n*5
; Efeitos:  (r0+1)*5
multiply:
	mov r1, #0
	add r0, r0, #1
loop_multiply:
	add r1, r1, #5
	sub r0, r0, #1 
	bne	loop_multiply
    mov r0, r1
	mov pc, lr


; Rotina:    delay
; Descricao: Rotina bloqueante que realiza uma espera ativa por teste sucessivo
;            do valor da variável global sysclk. O tempo a esperar, em
;            centenas de milissegundos, e passado em R0.
;            Interface exemplo: void delay( uint16_t hms );
; Entradas:  r0 = hms, tempo para dar delay
; Saidas:    -
; Efeitos:   -
delay:
	push lr	
	push r4 				; r4 = hms (5, 10, 20...)
	mov r4, r0				; salvar r0 em r4
	ldr r1, sysclk_addr
	mov r2, #0
	str r2, [r1, #0] 
	bl sysclk_get_ticks
	mov r3, r0 				; r3 = valor inicial do sysclk
delay_loop:
	bl sysclk_get_ticks		; r0 = valor atual do sysclk
	sub r0, r0, r3
	cmp r0, r4
	bge end_loop
	b delay_loop
end_loop:
	pop r4
	pop pc

; Rotina:    sysclk_get_ticks
; Descricao: Devolve o valor corrente da variável global sysclk.
;            Interface exemplo: uint16_t sysclk_get_ticks ( );
; Entradas:  -
; Saidas:    r0 - valor de sysclk
; Efeitos:   -
sysclk_get_ticks:
	ldr	r0, sysclk_addr
	ldr	r0, [r0, #0]
	mov	pc, lr



; Rotina:    isr
; Descricao: Incrementa o valor da variável global sysclk.
; Entradas:  -
; Saidas:    -
; Efeitos:   r0 e r1
isr:
    push    r0
    push    r1

    ldr 	r0, PTC_ADDR
	strb 	r0, [r0, #PTC_TIR]

    ldr    r0, sysclk_addr
    ldr    r1, [r0, #0]
    add    r1, r1, #1
    str    r1, [r0, #0]
	
	pop    r1
    pop    r0
    movs    pc, lr

sysclk_addr:
	.word sysclk

; Gestor de periférico para o porto de entrada
;

; Rotina:    inport_read
; Descricao: Adquire e devolve o valor corrente do porto de entrada.
;            Interface exemplo: uint8_t inport_read( );
; Entradas:  -
; Saidas:    R0 - valor adquirido do porto de entrada
; Efeitos:   -
inport_read:
	ldr	r1, inport_addr
	ldrb	r0, [r1, #0]
	mov	pc, lr

inport_addr:
	.word	INPORT_ADDRESS

; Gestor de periférico para o porto de saída
;

; Rotina:    outport_set_bits
; Descricao: Atribui o valor logico 1 aos bits do porto de saida identificados
;            com o valor 1 em R0. O valor dos outros bits nao e alterado.
;            Interface exemplo: void outport_set_bits( uint8_t pins_mask );
; Entradas:  R0 - Mascara com a especificacao do indice dos bits a alterar.
; Saidas:    -
; Efeitos:   Altera o valor da variavel global outport_img.
outport_set_bits:
	push	lr
	ldr	r1, outport_img_addr
	ldrb	r2, [r1, #0]
	orr	r0, r2, r0
	strb	r0, [r1, #0]
	bl	outport_write
	pop	pc

; Rotina:    outport_clear_bits
; Descricao: Atribui o valor logico 0 aos bits do porto de saida identificados
;            com o valor 1 em R0. O valor dos outros bits nao e alterado.
;            Interface exemplo: void outport_clear_bits( uint8_t pins_mask );
; Entradas:  R0 - Mascara com a especificacao do indice dos bits a alterar.
; Saidas:    -
; Efeitos:   Altera o valor da variavel global outport_img.
outport_clear_bits:
	push	lr
	ldr	r1, outport_img_addr
	ldrb	r2, [r1, #0]
	mvn	r0, r0
	and	r0, r2, r0
	strb	r0, [r1]
	bl	outport_write
	pop	pc

; Rotina:    outport_init
; Descricao: Faz a iniciacao do porto de saida, nele estabelecendo o valor
;            recebido em R0.
;            Interface exemplo: void outport_init( uint8_t value );
; Entradas:  R0 - Valor a atribuir ao porto de saida.
; Saidas:    -
; Efeitos:   Altera o valor da variavel global outport_img.
outport_init:
	push	lr
	ldr	r1, outport_img_addr
	strb	r0, [r1]
	bl	outport_write
	pop	pc

outport_img_addr:
	.word	outport_img

; Rotina:    outport_write
; Descricao: Escreve no porto de saida o valor recebido em R0.
;            Interface exemplo: void outport_write( uint8_t value );
; Entradas:  R0 - valor a atribuir ao porto de saida.
; Saidas:    -
; Efeitos:   -
outport_write:
	ldr	r1, outport_addr
	strb	r0, [r1, #0]
	mov	pc, lr

outport_addr:
	.word	OUTPORT_ADDRESS

; Gestor de periférico para o Pico Timer/Counter (pTC)
;

; Rotina:    ptc_start
; Descricao: Habilita a contagem no periferico pTC.
;            Interface exemplo: void ptc_start( );
; Entradas:  -
; Saidas:    -
; Efeitos:   -
ptc_start:
	ldr	r0, PTC_ADDR
	mov	r1, #PTC_CMD_START
	strb	r1, [r0, #PTC_TCR]
	mov	pc, lr

; Rotina:    ptc_stop
; Descricao: Para a contagem no periferico pTC.
;            Interface exemplo: void ptc_stop( );
; Entradas:  -
; Saidas:    -
; Efeitos:   O valor do registo TC do periferico e colocado a zero.
ptc_stop:
	ldr	r0, PTC_ADDR
	mov	r1, #PTC_CMD_STOP
	strb	r1, [r0, #PTC_TCR]
	mov	pc, lr

; Rotina:    ptc_get_value
; Descricao: Devolve o valor corrente da contagem do periferico pTC.
;            Interface exemplo: uint8_t ptc_get_value( );
; Entradas:  -
; Saidas:    R0 - O valor corrente do registo TC do periferico.
; Efeitos:   -
ptc_get_value:
	ldr	r1, PTC_ADDR
	ldrb	r0, [r1, #PTC_TC]
	mov	pc, lr

; Rotina:    ptc_init
; Descricao: Inicia uma nova contagem no periferico pTC com o intervalo de
;            contagem recebido em R0, em ticks.
;            Interface exemplo: void ptc_init( uint8_t interval );
; Entradas:  R0 - Valor do novo intervalo de contagem, em ticks.
; Saidas:    -
; Efeitos:   Inicia a contagem no periferico a partir do valor zero, limpando
;            o pedido de interrupcao eventualmente pendente.
ptc_init:
	push lr
	push r4
	mov r4, r0
	bl ptc_stop

	ldr    r1, PTC_ADDR
    strb    r4, [r1, #PTC_TMR] ; meter TMR a interval
	strb 	r1, [r1, #PTC_TIR] ; limpar PTC TIR

	bl ptc_start
	pop r4
    pop pc

PTC_ADDR:
	.word	PTC_ADDRESS

; Seccao:    .data
; Descricao: Guarda as variáveis globais com um valor inicial definido
;
	.data

; Seccao:    .bss
; Descricao: Guarda as variáveis globais sem valor inicial definido
;
	.section .bss
outport_img:
	.space	1
	.align

sysclk:
	.space	2

roulette_mask:
	.space 1

; Seccao:    .stack
; Descricao: Implementa a pilha com o tamanho definido pelo simbolo STACK_SIZE
;
	.section .stack
	.space	STACK_SIZE
tos:
