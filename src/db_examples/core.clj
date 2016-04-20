(ns db-examples.core
  (:require [clojure.java.jdbc :as sql]))

(def db {:subprotocal "postgresql"
         :subname "//locahost/reporting"
         :user "admin"
         :password "admin"})

(defn create-users-table! []
  (sql/db-do-commands db
                      (sql/create-table-ddl
                       :users
                       [:id "varchar(32) PRIMARY KEY"]
                       [:pass "varchar(100"])))

(defn get-user [id]
  (first
   (sql/query db ["select * from users where id = ?" id])))

(get-user "foo")

(defn add-user! [user]
  (sql/insert! db :users user))

(add-user! {:id "foo" :pass "bar"})

(defn add-users [& users]
  (apply sql/insert! db :users users))

(add-users!
 {:id "foo1" :pass "bar"}
 {:id "foo2" :pass "bar"}
 {:id "foo1" :pass "bar"})


;; “Alternatively, we can supply a vector containing the column IDs that we wish to insert followed by vectors containing the column values.”
(sql/insert! db :users [:id] ["bar"] ["baz"])


;; using the update! function
 ;; The function expects the connection, followed by the table name, the map representing the updated rows, and the where clause represented by a vector.”

(defn set-pass! [id pass]
  (sql/update!
   db
   :users
   {:pass pass}
   ["id=?" id]))

(set-pass)

;; DELETING RECORDS

(defn remove-user! [id]
  (sql/delete! db :users ["id=?" id]))

(remove-user! "foo")

;; TRANSACTIONS
;;  transactions-- when we want to run multiple statements and ensure that the statements will be executed only if all of them can be run successfully. If any of the statements fail, then the transaction will be rolled back to the state prior to running any of the statements.”

(sql/with-db-transaction [t-conn db]
  (sql/update!
   t-conn
   :users
   {:pass "bar"}
   ["id=?" "foo"])

  (sql/update!
   t-conn
   :users
   {:pass "baz"}
   ["id=?" "bar"]))
