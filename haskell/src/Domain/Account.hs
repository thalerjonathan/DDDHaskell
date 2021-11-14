{-# LANGUAGE Arrows             #-}
module Domain.Account where

import Control.Monad.Free
import Data.MonadicStreamFunction
import Data.MonadicStreamFunction.InternalCore
import Data.Text
import Data.Time.Clock
import Domain.Customer (CustomerId (CustomerId))
import Data.UUID.V4 (nextRandom)

newtype Iban = Iban Text deriving Show
type Money = Double
data TXLine = TXLine Money Iban Text Text UTCTime deriving Show

data AccountCommand
  = Deposit Money
  | Withdraw Money
  | TransferTo Iban Money Text Text
  | ReceiveFrom Iban Money Text Text
  | GetBalance 
  | GetOwner
  | GetIban 
  deriving Show

data AccountCommandResult
  = AccountException Text
  | ReturnBalance Money
  | ReturnOwner CustomerId
  | ReturnIban Iban
  deriving Show
  
data AccountDomainEvent
  = TransferSent Money Text CustomerId CustomerId Iban Iban
  | TransferFailed Text Money Text CustomerId CustomerId Iban Iban 
  deriving Show

data AccountLang a
  = ReadTXLines ([TXLine] -> a)
  | ReadOwner (CustomerId -> a)
  | ReadIban (Iban -> a)
  | NewTXLine Money Iban Text Text a
  | EmitEvent AccountDomainEvent a
  deriving Functor 

type AccountProgram = Free AccountLang

data AccountState 
  = AccountState CustomerId Iban [TXLine] deriving Show

type Account = MSF AccountProgram AccountCommand (Maybe AccountCommandResult)

-- TODO: deal with repository?

execCommand :: Account -> AccountCommand -> IO (Account, (Maybe AccountCommandResult, [AccountDomainEvent]))
execCommand a cmd = do
  ((ret, a'), es) <- interpret (unMSF a cmd) []
  return (a', (ret, es))

execCommands :: Account -> [AccountCommand] -> IO ([Maybe AccountCommandResult], [AccountDomainEvent])
execCommands a cmds = do
  (ret, es) <- interpret (embed a cmds) []
  return (ret, es)

account :: CustomerId -> Iban -> Account
account cid ib = feedback s0 (proc (cmd, s) -> do
    ret <- arrM handleCommand -< cmd
    returnA -< (ret, s))
  where
    s0 = AccountState cid ib []

txLines :: AccountProgram [TXLine]
txLines = liftF (ReadTXLines id)

newTxLine :: Money -> Iban -> Text -> Text -> AccountProgram ()
newTxLine m i name ref = liftF (NewTXLine m i name ref ())

owner :: AccountProgram CustomerId
owner = liftF (ReadOwner id)

iban :: AccountProgram Iban
iban = liftF (ReadIban id)

emitEvent :: AccountDomainEvent -> AccountProgram ()
emitEvent evt = liftF (EmitEvent evt ())

balance :: [TXLine] -> Money
balance = Prelude.foldr (\(TXLine m _ _ _ _) acc -> acc + m) 0

overdraftLimit :: Double
overdraftLimit = -1000

handleCommand :: AccountCommand -> AccountProgram (Maybe AccountCommandResult)
handleCommand (Withdraw amount) = do
  bal <- balance <$> txLines
  if bal - amount < overdraftLimit
    then do
      let ret = AccountException "Cannot overdraw Giro account by more than -1000!"
      return $ Just ret
    else do
      i <- iban
      newTxLine (-amount) i "Withdraw" "Withdraw" 
      return Nothing
handleCommand (Deposit amount) = do
  i <- iban
  newTxLine amount i "Deposit" "Deposit" 
  return Nothing
handleCommand (TransferTo toIban amount name ref) = do
  myIban <- iban
  o <- owner
  -- TODO: wrong data so far, use correct
  emitEvent $ TransferSent amount ref o o myIban toIban
  newTxLine (-amount) toIban name ref
  return Nothing
handleCommand (ReceiveFrom fromIban amount name ref) = do
  myIban <- iban
  o <- owner
  -- TODO: wrong data so far, use correct
  emitEvent $ TransferFailed "TestError" amount ref o o myIban fromIban
  newTxLine amount fromIban name ref
  return Nothing
handleCommand GetBalance = do
  Just . ReturnBalance . balance <$> txLines
handleCommand GetOwner = do
  Just . ReturnOwner <$> owner
handleCommand GetIban = do
  Just . ReturnIban <$> iban

interpret :: AccountProgram a -> [AccountDomainEvent] -> IO (a, [AccountDomainEvent])
interpret (Pure a) acc = return (a, Prelude.reverse acc)
interpret (Free (ReadTXLines contF)) acc = do
  putStrLn "ReadTXLines"
  interpret (contF []) acc
interpret (Free (ReadOwner contF)) acc = do
  putStrLn "ReadOwner"
  uuid <- nextRandom
  interpret (contF (CustomerId uuid)) acc
interpret (Free (ReadIban contF)) acc = do
  putStrLn "ReadIban"
  interpret (contF (Iban "AT12 12345 01234567890")) acc
interpret (Free (NewTXLine m i name ref cont)) acc = do
  putStrLn "NewTXLine"
  t <- getCurrentTime
  let _tx = TXLine m i name ref t
  interpret cont acc
interpret (Free (EmitEvent e cont)) acc = do
  putStrLn $ "EmitEvent" ++ show e
  interpret cont (e:acc)