(vector 1 2 3)
; => [1 2 3]

[1 2 3]
; => [1 2 3]

(read-string "[1 2 3]")
; => [1 2 3]

(= (vector 1 2 3)
   (read-string "[1 2 3]"))
; => true

(read-string "(vector 1 2 3)")
; => (vector 1 2 3)

(list 1 2 3)
; => (1 2 3)

(1 2 3)
; => ClassCastException
;  java.lang.Long cannot
;  be cast to clojure.lang.IFn

(read-string "(1 2 3)")
; => (1 2 3)

(read-string "(+ 1 2 3)")
; => (+ 1 2 3)

(eval 
  (read-string "(+ 1 2 3)"))
; => 6

(eval '(+ 1 2 3))
;; => 6