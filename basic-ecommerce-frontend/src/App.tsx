import './App.css';
import { BrowserRouter, Routes, Route} from 'react-router-dom';
import { CartProvider } from "./context/CartContext"

import Login from './login/Login';
import SignUp from './sign-up/SignUp';
import Home from './home/Home';
import Cart from './cart/Cart';

function App() {

  return (
      <BrowserRouter>
        <CartProvider>
          <Routes>
            <Route path="/login" element={<Login />} />
            <Route path="/" element={<Home />} />
            <Route path="/home" element={<Home />} />
            <Route path="/sign-up" element={<SignUp />} />
            <Route path="/my-cart" element={<Cart />} />
          </Routes>
        </CartProvider>
      </BrowserRouter>
  )
}

export default App;