module Domain.Customer where

import Data.UUID

newtype CustomerId = CustomerId UUID deriving Show