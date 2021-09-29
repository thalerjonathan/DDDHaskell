module Main where

import Aggregate

main :: IO ()
main = do
  let s   = 0 :: Int
      agg = aggregate s

  (agg', es) <- execCommand agg CommandA
  (_, _) <- execCommand agg' CommandB

  putStrLn $ "Aggregate emitted DomainEvents: " ++ show es
