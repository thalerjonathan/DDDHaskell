module Main where

import Aggregate

main :: IO ()
main = do
  let agg = testAggregate

  (agg', es) <- execCommand agg CommandA
  (_, _) <- execCommand agg' CommandB

  putStrLn $ "Aggregate emitted DomainEvents: " ++ show es
