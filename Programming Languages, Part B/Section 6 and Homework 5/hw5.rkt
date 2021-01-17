;; Programming Languages, Homework 5

#lang racket
(provide (all-defined-out)) ;; so we can put tests in a second file

;; definition of structures for MUPL programs - Do NOT change
(struct var  (string) #:transparent)  ;; a variable, e.g., (var "foo")
(struct int  (num)    #:transparent)  ;; a constant number, e.g., (int 17)
(struct add  (e1 e2)  #:transparent)  ;; add two expressions
(struct ifgreater (e1 e2 e3 e4)    #:transparent) ;; if e1 > e2 then e3 else e4
(struct fun  (nameopt formal body) #:transparent) ;; a recursive(?) 1-argument function
(struct call (funexp actual)       #:transparent) ;; function call
(struct mlet (var e body) #:transparent) ;; a local binding (let var = e in body) 
(struct apair (e1 e2)     #:transparent) ;; make a new pair
(struct fst  (e)    #:transparent) ;; get first part of a pair
(struct snd  (e)    #:transparent) ;; get second part of a pair
(struct aunit ()    #:transparent) ;; unit value -- good for ending a list
(struct isaunit (e) #:transparent) ;; evaluate to 1 if e is unit else 0

;; a closure is not in "source" programs but /is/ a MUPL value; it is what functions evaluate to
(struct closure (env fun) #:transparent) 

;; Problem 1

;; CHANGE (put your solutions here)
(define (racketlist->mupllist lst)
  (cond [(null? lst) (aunit)]
        [#t (apair (car lst) (racketlist->mupllist (cdr lst)))]))
(define (mupllist->racketlist lst)
  (cond [(aunit? lst) null]
        [#t (cons (apair-e1 lst) (mupllist->racketlist (apair-e2 lst)))]))
;; Problem 2

;; lookup a variable in an environment
;; Do NOT change this function
(define (envlookup env str)
  (cond [(null? env) (error "unbound variable during evaluation" str)]
        [(equal? (car (car env)) str) (cdr (car env))]
        [#t (envlookup (cdr env) str)]))

;; Do NOT change the two cases given to you.  
;; DO add more cases for other kinds of MUPL expressions.
;; We will test eval-under-env by calling it directly even though
;; "in real life" it would be a helper function of eval-exp.
(define (eval-under-env e env)
  (cond [(var? e) 
         (envlookup env (var-string e))]
        [(add? e) 
         (let ([v1 (eval-under-env (add-e1 e) env)]
               [v2 (eval-under-env (add-e2 e) env)])
           (if (and (int? v1)
                    (int? v2))
               (int (+ (int-num v1) 
                       (int-num v2)))
               (error "MUPL addition applied to non-number")))]
        ;; CHANGE add more cases here
        [(int? e) e]
        [(ifgreater? e)
         (let ([v1 (eval-under-env (ifgreater-e1 e) env)]
               [v2 (eval-under-env (ifgreater-e2 e) env)])
           (if (and (int? v1)
                    (int? v2))
               (if (> (int-num v1) (int-num v2))
                   ; DO NOT eval v3 and v4 before condition is correct
                   (eval-under-env (ifgreater-e3 e) env)
                   (eval-under-env (ifgreater-e4 e) env))
               (error "MUPL ifgreater applied to non-number")))]
        [(fun? e) (closure env e)]
        [(closure? e) e]
        [(mlet? e)
         (let ([var (mlet-var e)]
               [value (eval-under-env (mlet-e e) env)])
           (if (string? var)
               (let ([extenv (cons (cons var value) env)])
                 (eval-under-env (mlet-body e) extenv))
               (error "MUPL mlet applied to non-string")))]
        [(call? e)
         (let ([func (eval-under-env (call-funexp e) env)]
               [argv (eval-under-env (call-actual e) env)])
           (if (closure? func)
               (let ([f (closure-fun func)]
                     [e (closure-env func)])
                 (let ([fn (fun-nameopt f)]
                       [ff (fun-formal f)]
                       [fb (fun-body f)])
                     (let ([argenv (cons (cons ff argv) e)])
                       (if (string? fn)
                           ; NOTE: This is (cons fn func), not (cons fn f)
                           (let ([recargenv (cons (cons fn func) argenv)])
                             (eval-under-env fb recargenv))
                           (eval-under-env fb argenv)))))
               (error "MUPL call applied to non-closure")))]
        [(apair? e)
         (let ([v1 (eval-under-env (apair-e1 e) env)]
               [v2 (eval-under-env (apair-e2 e) env)])
           (apair v1 v2))]
        [(fst? e)
         (let ([p (eval-under-env (fst-e e) env)])
           (if (apair? p)
               (let ([v (eval-under-env (apair-e1 p) env)])
                 v)
               (error "MUPL fst applied to non-pair")))]
        [(snd? e)
         (let ([p (eval-under-env (snd-e e) env)])
           (if (apair? p)
               (let ([v (eval-under-env (apair-e2 p) env)])
                 v)
               (error "MUPL fst applied to non-pair")))]
        [(isaunit? e)
         (let ([u (eval-under-env (isaunit-e e) env)])
           (if (aunit? u)
               (int 1)
               (int 0)))]
        [(aunit? e) e]
        [#t (error (format "bad MUPL expression: ~v" e))]))

;; Do NOT change
(define (eval-exp e)
  (eval-under-env e null))
        
;; Problem 3

(define (ifaunit e1 e2 e3)
  (ifgreater (isaunit e1) (int 0)
             e2
             e3))

(define (mlet* lstlst e2)
  (if (null? lstlst)
      e2
      (let ([e (car lstlst)]
            [lst (cdr lstlst)])
        (mlet (car e) (cdr e)
              (mlet* lst e2)))))

(define (ifeq e1 e2 e3 e4)
  (mlet "_x" e1
        (mlet "_y" e2
              (mlet "_e4" e4
                    (ifgreater (var "_x") (var "_y")
                               (var "_e4")
                               (ifgreater (var "_y") (var "_x")
                                          (var "_e4")
                                          e3))))))

;; Problem 4

(define mupl-map
  (fun #f "_mupl-map-f"
       (fun "_mupl-map-recursive" "_mupl-map-acc"
            (ifaunit (var "_mupl-map-acc")
                     (aunit)
                     (mlet "_mupl-map-e" (fst (var "_mupl-map-acc"))
                           (mlet "_mupl-map-acc-cdr" (snd (var "_mupl-map-acc"))
                                 (mlet "_mupl-map-v" (call (var "_mupl-map-f") (var "_mupl-map-e"))
                                       (apair (var "_mupl-map-v") (call (var "_mupl-map-recursive") (var "_mupl-map-acc-cdr"))))))))))
  

(define mupl-mapAddN 
  (mlet "map" mupl-map
        (fun #f "_mupl-map-add-n"
             (call mupl-map (fun #f "_mupl-map-element"
                                 (add (var "_mupl-map-element") (var "_mupl-map-add-n")))))))

;; Challenge Problem

(struct fun-challenge (nameopt formal body freevars) #:transparent) ;; a recursive(?) 1-argument function

;; We will test this function directly, so it must do
;; as described in the assignment
(define (compute-free-vars e) "CHANGE")

;; Do NOT share code with eval-under-env because that will make
;; auto-grading and peer assessment more difficult, so
;; copy most of your interpreter here and make minor changes
(define (eval-under-env-c e env) "CHANGE")

;; Do NOT change this
(define (eval-exp-c e)
  (eval-under-env-c (compute-free-vars e) null))
