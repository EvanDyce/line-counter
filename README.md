# What it Does
This java program counts the number of files, lines, and sizes of different files within the specified directory. Files are sorted by file ending, java, py, txt, etc. Additional information can be displayed depending on the command line options chosen. 

## How to Use it
When running the program without any command line options it will simply list the number of files for every type within the directory. However with more command line flags you are able to customize the search.

## Command Line Options
- -p --path
  - specifiy the path to the directory you want to search. 
  - Ex. "java -jar yourjar.jar -p %USERPROFILE%/downloads" will search your downloads folder
  
- -f -- find 
  - search for files that end with the specified ending. Can be used more than once.
  - Ex. "java -jar yourjar.jar -f py -f java" will search the directory for any py or java files

- -e --exclude 
  - exclude certain file from the output. Can be used more than once.
  - Ex. "java -jar yourjar.jar --exclude jpg" will exclude jpg from the search.

- -lc --linecount
  - display the linecount along with the number of files of each type
  - Ex. "java -jar yourjar.jar -f java -lc" will find all java files and print the number of files along with the combined linecount of all of the files.
  
- -s --size
  - display the size of each type of file
  - Ex. "java -jar yourjar.jar --size -lc -f txt" will find all text files and display the number of lines as well as teh size of all the text files combined.
 
