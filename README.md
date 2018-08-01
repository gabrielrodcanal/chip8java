# chip8java
## A simple CHIP-8 emulator

### INTRODUCTION

This is my first emulator and my first public programming project as well. It is probably not the best CHIP-8 emulator around the Internet but somebody might find this interesting for educational purposes. It is able to successfully load all the CHIP-8 ROMs (some might need a little quirk detailed here: https://github.com/tomdaley92/Kiwi8/issues/9). 


### COMPILATION

In order to compile the emulator execute the next command:

javac src/chip8/*.java

(Make sure you have Java JRE 8 installed in your system before trying to compile).

The .jar file will appear under dist/ directory. To launch the emulator use:

java -jar dist/chip8.jar rompath shift_quirk load_store_quirk magnify_factor wave_type

where:
  - rompath: ROM gamepath you want to play
  - shift_quirk: should be true or false
  - load_store_quirk: should be true or false
  - magnify_factor: should be a natural number. It multiplies the original screen size by this number (WARNING: a too high number 
  might result in poor performance).
  -wave_type:
    - 0: square
    - 1: saw-tooth
    - 2: triangle
    - 3: sine (WARNING: due to the low frequency chosen [200 Hz] some speakers might not be able to play the sound accurately. In
    my case, my laptop's speakers are unable to play it, but my headset is)
    
### KEYBOARD

The CHIP-8 original keyboard is:


\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\
| 1 | 2 | 3 | C |\
\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\
| 4 | 5 | 6 | D |\
\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\
| 7 | 8 | 9 | E |\
\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\
| A | 0 | B | F |\
\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-


And it has been mapped to:


\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\
| 1 | 2 | 3 | 4 |\
\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\
| q | w | e | r |\
\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\
| a | s | d | f |\
\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\
| z | x | c | v |\
\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-

#### Final note on quirks: Make sure they are enabled or disabled properly according to this guide: https://github.com/tomdaley92/Kiwi8/issues/9
