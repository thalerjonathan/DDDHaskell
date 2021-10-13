{-# LANGUAGE DeriveFunctor #-}
{-# LANGUAGE Arrows        #-}
module Aggregate where

import Control.Monad.Free
import Data.MonadicStreamFunction
import Data.MonadicStreamFunction.InternalCore

data Command
  = CommandA 
  | CommandB
  deriving Show

data DomainEvent
  = EventA
  | EventB
  deriving Show

data AggregateLang a
  = Foo a
  | FooBar (String -> a)
  deriving Functor 

type AggregateProgram = Free AggregateLang

type Aggregate = MSF AggregateProgram Command DomainEvent

-- TODO: how can we have an aggregate root with multiple entities?

-- TODO: an aggregate should not run in IO but be pure. probably use state monad 
execCommand :: Aggregate -> Command -> IO (Aggregate, [DomainEvent])
execCommand agg cmd = do
  (es, agg') <- interpret (unMSF agg cmd)
  return (agg', [es])

aggregate :: s -> Aggregate
aggregate s0 = feedback s0 (proc (cmd, s) -> do
  _ <- arrM handleCommand -< cmd
  returnA -< (EventA, s))

foo :: AggregateProgram ()
foo = liftF (Foo ())

fooBar :: AggregateProgram String
fooBar = liftF (FooBar id)

handleCommand :: Command -> AggregateProgram ()
handleCommand CommandA = do
  foo
handleCommand CommandB = do
  _str <- fooBar 
  return ()

interpret :: AggregateProgram a -> IO a
interpret (Pure a) = return a
interpret (Free (Foo cont)) = do
  putStrLn "Foo"
  interpret cont
interpret (Free (FooBar contF)) = do
  putStrLn "FooBar"
  interpret (contF "string")