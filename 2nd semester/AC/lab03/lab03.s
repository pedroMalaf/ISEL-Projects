; Ficheiro:  lab03.S
; Descricao: Programa para a realização da 3a atividade laboratorial de
;            Arquitetura de Computadores.
; Autor:     Grupo 12 LEIC22D
; Data:      04-05-2022

; Definicao dos valores dos simbolos utilizados no programa
;
	.equ	STACK_SIZE, 64                ; Dimensao do stack - 32 B
	.equ	INPORT_ADDRESS, 0xFF00       ; Endereço do porto de entrada
	.equ	OUTPORT_ADDRESS, 0xFF00       ; Endereço do porto de saída

; Seccao:    .startup
; Descricao: Guarda o código de arranque do sistema
;
	.section .startup
	b	_start
	b	.
_start:
	ldr	sp, tos_addr
	ldr	pc, main_addr

tos_addr:
	.word	tos
main_addr:
	.word	main

; Seccao:    .text
; Descricao: Guarda o código do programa
;
	.text

; Rotina:    main
; Descricao: Ao iniciar pisca os leds do porto de saida, de seguida fica num loop em que 
; Entradas:  -
; Saidas:    -
; Efeitos:   
main:
	mov	r0, #0xFF
	bl	outport_write
	mov	r0, #0x00
	bl	outport_write
loop:
	bl	inport_read
	bl	outport_write
	b	loop

; Rotina:    inport_read
; Descricao: Lê o valor que está no porto de entrada
; Entradas:  -
; Saidas:    r0 - Valor do porto de entrada
; Efeitos:   r1 - Edereço do porto de entrada
inport_read:
	ldr	r1, inport_addr
	ldrb	r0, [r1, #0]
	mov	pc, lr

inport_addr:
	.word	INPORT_ADDRESS

; Rotina:    outport_write
; Descricao: Escreve no porto de saída
; Entradas:  r0 - Valor que será passado ao porto de sáida
; Saidas:    -
; Efeitos:   r1 - Edereço do porto de saída
outport_write:
	ldr	r1, outport_addr
	strb	r0, [r1, #0]
	mov	pc, lr

outport_addr:
	.word	OUTPORT_ADDRESS

; Rotina:    sleep
; Descricao: O programa preso nesta rotina durante r0 * 100 ms 
; Entradas:  r0 - Este parametro indica o número de ms que se obtém multiplicando ele próprio por 100 ( r0 = 1 <=> +- 100 ms)
; Saidas:    -
; Efeitos:    Fica nesta rotina durante 2*3 + r0*(2*3 + 2*3*830 + 1) ms
sleep:
	and	r0, r0, r0
	beq	sleep_end
sleep_outer_loop:
	mov	r1, #0x3E
	movt	r1, #0x03
sleep_inner_loop:
	sub	r1, r1, #1
	bne	sleep_inner_loop
	sub	r0, r0, #1
	bne	sleep_outer_loop
sleep_end:
	mov	pc, lr

; Seccao:    .data
; Descricao: Guarda as variáveis globais com um valor inicial definido
;
	.data

; Seccao:    .bss
; Descricao: Guarda as variáveis globais sem valor inicial definido
;
	.section .bss

; Seccao:    .stack
; Descricao: Implementa a pilha com o tamanho definido pelo simbolo STACK_SIZE
;
	.section .stack
	.space	STACK_SIZE
tos:
