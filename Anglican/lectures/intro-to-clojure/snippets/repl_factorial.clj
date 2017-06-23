examples.core=> (require 'examples.factorial)
;; => nil

examples.core=> (in-ns 'examples.factorial)
;; => #object[clojure.lang.Namespace 0x42cd2abe "examples.factorial"]

examples.factorial=> (-main "1" "2" "5" "20")
;; => the factorial of 1 is 1
;; => the factorial of 2 is 2
;; => the factorial of 5 is 120
;; => the factorial of 20 is 2432902008176640000
;; => nil