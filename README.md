# Formula Cipher
Cryptosystem in an Android app that uses several methods to ensure security.

Formula Cipher 1.3,
Formula Encryption 2.0

The code for Formula Encryption 1.0 ("FormulaC" - "C" for the language) was first developed and tested in C with Arduino. The creation of the Android app ("FormulaJ" - "J" for Java) has come with major improvements, and thus making version 2.0.

The Formula Cipher app is an Android app that uses Formula Encryption to encrypt messages. For Marshmallow, the navigation bar color is modified and the apk can be found in /app. For others, or if you prefer, use the legacy apk also found in /app.

# Formula Encryption

Formula Encryption is a secure but experimental cryptosystem that works by encrypting a plaintext and generating a public key and the corresponding ciphertext. Thus, decryption works by inputting the ciphertext and public key into the algorithm to compute the corresponding plaintext.

# Public key, private key and ciphertext

Public keys are generated randomly (or almost). It is a very long string of digits, however, the string is formatted in the algorithm by breaking it down into arrays of 9. There are 3 arrays per character. For example, "Hello world" has 11 characters, 33 arrays and a 99 digit-long public key. The reason why the public key is so long is that there are many nulls in the public key that are not needed but are just to add confusion. Of course, the algorithm knows exactly which digits to ignore and which to pick up on.

So what are the secrets about the public key, private key and ciphertext? For most 2-key cryptosystems, and also true in Formula Encryption, the private key is used to form the public key, which is then used with the plaintext to form the ciphertext. Therefore, decryption is done by using the private key to format the public key, which is used with the ciphertext to form the plaintext. Here, I mentioned format. The public key is first split into arrays of 9, then built in groups of 3 arrays (as mentioned above). It is then reduced by removing the nulls. In this algorithm, the nulls are the digits that are used in the public key but not in the private key (there must be 3). That means, when choosing a private key, it must only use 6 out of the 9 available digits to choose from (1 to 9). An example is shown in the screenshots, and also is set as the default. There are, in fact, more nulls to remove from the public key: the digits that do not match their index for every 9-digit array (1-indexed). This leaves only 1 digit in each array, and so this is inputted into a formula. The formula can only be edited in the Java code. For advice on using different formulae, I suggest you do not include 0s if you divide because you will need to handle the divide-by-zero exception. A simple formula is still secure. The formula computes the ciphertext indeces of the alphabet used in the algorithm. The plaintext and ciphertext share the same alphabet. The indeces are then inputted in the charAt() function to find the corresponding characters, which are then strung together.
