all:	
	gcc -c -std=gnu99 -Wall -Werror -fpic pi_dht_read.c pi_mmio.c common_dht_read.c
	gcc -shared -lrt -o libPiDht.so *.o
	
clean:	
	rm *.o *.so

install:
	sudo cp libPiDht.so /usr/lib/

uninstall:
	sudo rm /usr/lib/libPiDht.so
