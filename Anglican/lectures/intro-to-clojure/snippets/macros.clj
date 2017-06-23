(defmacro unless
  "Inverted 'if"
  [pred then else]
  (list 'if pred else then))

(def flavor :tasty)

(unless (= flavor :tasty)
  :yuk
  :yum))

; ~>

(if (= flavor :tasty) 
  :yum 
  :yuk)

; => 

:yum 

(macroexpand
  '(unless (tasty? food)
     :yuk
     :yum))
; => (if (tasty? food)
;      :yum
;      :yuk))

(defmacro dbg 
  "Prints an expression and
  its value for debugging."
  [expr]
  (list 'do 
    (list 'println 
      "[dbg]" 
      (list 'quote expr) 
      expr)
    expr))

(dbg (+ 1 2))
; => [dbg] (+ 1 2) 3
; => 3

(macroexpand '(dbg (+ 1 2))
; => (do 
;      (println "[dbg]" 
;               (quote (+ 1 2)) 
;               (+ 1 2)) 
;      (+ 1 2))

(defmacro dbg 
  "Prints an expression and
  its value for debugging."
  [expr]
  `(let [value# ~expr] 
    (println "[dbg]" '~expr value#) 
    value#))

(dbg (+ 1 2))
; => [dbg] (+ 1 2) 3
; => 3

(macroexpand '(dbg (+ 1 2))
; => (let* [value__23707__auto__ (+ 1 2)] 
;      (clojure.core/println 
;        "[dbg]" 
;        (quote (+ 1 2)) 
;        value__23707__auto__) 
;      value__23707__auto__)
