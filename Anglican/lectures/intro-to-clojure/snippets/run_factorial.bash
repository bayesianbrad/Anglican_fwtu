# get source code for this tutorial
git clone git@bitbucket.org:probprog/ppaml-summer-school-2016.git
cd ppaml-summer-school-2016/lectures/
cd intro-to-functional-programming/examples/ 

# option 1: build uberjar and run via java
lein uberjar
java -cp target/uberjar/examples-0.1.0-SNAPSHOT.jar \ 
	examples.factorial 1 2 5 20

# option 2: run using leiningen
lein run -m examples.factorial 1 2 5 20

# => the factorial of 1 is 1
# => the factorial of 2 is 2
# => the factorial of 5 is 120
# => the factorial of 20 is 2432902008176640000