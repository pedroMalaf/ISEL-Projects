; -----------------------------------------------------------------------------
; Ficheiro:  lab01.S
; Descricao: Codigo da 1a atividade de laboratorio optimizado
; Autores:	 - Tiago M Dias (tiago.dias@isel.pt)
;			 - Carolina Pereira (A49470@alunos.isel.pt)
;			 - Pedro Malafaia (A49506@alunos.isel.pt)
;			 - Roberto Petrisoru (A49418@alunos.isel.pt)
; Data:		 17-03-2022
; -----------------------------------------------------------------------------

	; r0 guarda o valor da variavel M
	; r1 guarda o valor da variavel m
	; r2 guarda o valor da variavel p
	; r3 e utilizado para guardar valores temporariamente

	mov	r2, #0

    cmp r0, r1
    bhs if
    mov r3, r0
    mov r0, r1
    mov r1, r3

if:
	mov r3, #0
	cmp r0, r3
	beq if_end
while:
	mov	r3, #0
	cmp	r3, r1
	bhs	while_end
	add	r2, r2, r0
	sub	r1, r1, #1
	b	while
while_end:
if_end:
	b	.
