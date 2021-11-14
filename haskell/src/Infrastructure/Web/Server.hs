module Infrastructure.Web.Server
  ( startApp 
  ) where

import Network.Wai
import Network.Wai.Handler.Warp

-- development serves over port 3000 (for react)
port :: Int
port = 8080

-- developement always over normal HTTP
startApp :: Application -> IO ()
startApp = run port