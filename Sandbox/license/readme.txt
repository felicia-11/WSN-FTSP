There are two ways how you can place your license keys which the build system understands. Each system is identified by it's unique
identifier (UID). Each license key is valid for only one UID.


1.	Create one license key file for each system. The name of the key file is the hex encoded UID of the system suffixed with .txt.
	The file itself contains the ASCII hey encoded license key code.

2.	Place your license keys inside the keys file which is named keys.txt. Each line of the file contains a hex encoded UID and the
	corresponding hex encoded license key seperated by a = character. Therefore each line contains a pair of UID and
	hex encoded license key.
	
	Example content of a keys.txt file:
	430848243630503105DBFF31 = 1234567890ab13bfa596895b275e0762c56743abb764df275f31abc75808d55fc3501bdf80950df54dc547eee4d1aec2405158dff8d7c198d4be6d650e292f45
	430848243630503105DBFF32 = 369f233a6bab13bfa596895b275e0762c56743abb764df275f31abc75808d55fc3501bdf80950df54dc547eee4d1aec2405158dff8d7c198d4be6d650e292f45