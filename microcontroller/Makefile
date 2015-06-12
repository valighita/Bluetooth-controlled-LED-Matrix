all: matrix.hex
 
matrix.elf: matrix.c	
	avr-gcc -mmcu=atmega324a -Os -Wall -std=c99 -o matrix.elf matrix.c
 
matrix.hex: matrix.elf
	avr-objcopy  -j .text -j .data -O ihex  matrix.elf matrix.hex
	avr-size matrix.elf
 
clean:
	rm -rf matrix.elf matrix.hex