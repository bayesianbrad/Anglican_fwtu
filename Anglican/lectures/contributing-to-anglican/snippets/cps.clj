(query [outcome]
 (let [theta (sample (beta 1 1))]
  (observe (flip theta) outcome)
  theta))

(fn one-flip [outcome state cont]
 (sample (beta 1 1) 
  state 
  (fn [theta state] 
   (observe (flip theta) outcome
    state
    (fn [_ state]
     (cont nil
      (add-predict 
       state bias)))))))

(defn importance-one-flip 
  [outcome]
  (let [theta (sample* (beta 1 1))
        lp (observe* (flip theta) 
                    outcome)]
    {:log-weight lp
     :result theta 
     :predicts []}))


(defn mh-one-flip 
  [outcome prev-samples]
  (let [theta (sample* (beta 1 1))
        w (observe* (flip theta) 
                    outcome)]
    {:log-weight w 
     :result theta 
     :predicts []
     :cache }))


(fn [outcome $state]
 (->sample 'S24726
  (beta 1 1)
  (fn [theta $state]
   (->observe 'O24724
    (flip theta) 
    outcome
    (fn [_ $state] 
     (->result theta $state))
    $state))
  $state))

{:id 'S24726
 :dist (beta 1 1)
 :cont (fn [theta $state]
         ...)
 :state $state}

{:id 'O24724
 :dist (flip theta) 
 :value outcome
 :cont (fn [_ $state] 
         (->result theta $state))
 :state $state}

{:result theta 
 :state $state}

{:result theta
 :log-weight 
   (:log-weight $state)
 :predicts 
   (:predicts $state)}

(use '[anglican emit runtime inference state])
(exec :importance one-flip [true] initial-state)
; =>
#anglican.trap.sample{:id S23882, 
                      :dist (anglican.runtime/beta 1 1), 
                      :cont #function[test/fn--23883$query23877--23885$var23881--23887], 
                      :state {:log-weight 0.0, 
                              :predicts [], 
                              :result nil, 
                              :anglican.state/mem {}, 
                              :anglican.state/store nil}}  

(let [x (sample* dist)]
  (cont x $state)) 

(let [w (observe* dist value)]
  (cont nil (add-log-weight $state w)))