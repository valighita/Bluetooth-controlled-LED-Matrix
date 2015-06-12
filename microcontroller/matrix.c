#include <avr/io.h>
#include <avr/interrupt.h>
#define F_CPU 16000000
#include <util/delay.h>

#define INCR PD5
#define RESET PD6

/* a line in the matrix has 8 leds, so we can use a byte for each line */
char bytes[8] = {0,0,0,0,0,0,0,0};

int byte_index = 0;

/* Interrupt handler executed when a byte is received from the USART interface (from the bluetooth module)
*  The handler saves the byte on the corresponding position in bytes vector
*/
ISR(USART0_RX_vect) {
	bytes[byte_index ++] = UDR0;
	if(byte_index > 7) {
		byte_index = 0;
	}

	PORTB ^= (1 << PB5);

	UCSR0B |= (1 << RXEN0);
}

/* Serial interface init, for the communication with the bluetooth module */
void initUSART() {

	DDRD |= (1 << PD1);
	DDRD &= ~(1 << PD0);

	UBRR0L = 103; // baud rate 9600
	UCSR0C &= ~(1 << USBS0); // 1 stop bit
	UCSR0C &= ~((1 << UPM01) | (1 << UPM00)); // no parity
	UCSR0B |= (1 << RXCIE0) | (1 << RXEN0); // enable RX and TX interrupts
}

int main(void)
{
	sei();

	initUSART();

	char* display = bytes;

	DDRB |= (1 << PB5);
	DDRD |= (1 << INCR);
	DDRD |= (1 << RESET);

	DDRA = 255;

	int row = 0;

	/* itterate through the rows and control the leds in the
	 * row using the values in the display array */
	while(1) {
		if(row == 0) {
			PORTD |= (1 << RESET);
			_delay_ms(1);
			PORTD &= ~(1 << RESET);
		}

		PORTD |= (1 << INCR);
		PORTA = display[row];
		_delay_ms(1);
		PORTD &= ~(1 << INCR);

		row++;
		if(row > 7) {
			row = 0;
		}
	}

	return 0;
}
