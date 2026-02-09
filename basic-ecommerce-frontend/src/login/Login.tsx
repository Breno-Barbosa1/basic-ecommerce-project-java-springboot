import './Login.css';

import { useNavigate } from 'react-router-dom';
import { useState} from 'react';

export default function Login() {
    const API_URL = import.meta.env.VITE_API_URL || "http://localhost:8080";

    const navigate = useNavigate();
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');

    const handleLogin = async (e: any) => {
        e.preventDefault();
    
        try {
            const response = await fetch(`${API_URL}/auth/login`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ email, password }),
            });

        if (response.ok) {
            const data = await response.json();
            
            localStorage.setItem("token", data.body.accessToken);
            localStorage.setItem("userEmail", data.body.email);

            navigate('/home');
            } else {
                alert('Login failed. Please check your credentials and try again.');
            }
        } catch (error) {
            console.error('Error during login:', error);
            alert('An error occurred. Please try again later.');
        }
    };   

    return (
        <div className="login-container">
        <div className="login-card">
            <div className="login-header">
                <h2>Sign In</h2>
                <p>Enter your credentials to access your account</p>
            </div>

            <form className="login-form" id="loginForm" onSubmit={handleLogin}>
                <div className="form-group">
                    <div className="input-wrapper">
                        <input type="email" id="email" name="email" onChange={(e) => setEmail(e.target.value)} />
                        <label htmlFor="email">Email Address</label>
                    </div>
                    <span className="error-message" id="emailError"></span>
                </div>

                <div className="form-group">
                    <div className="input-wrapper password-wrapper">
                        <input type="password" id="password" name="password" onChange={(e) => setPassword(e.target.value)} />
                        <label htmlFor="password">Password</label>
                    </div>
                    <span className="error-message" id="passwordError"></span>
                </div>

                <div className="form-options">
                    <label className="remember-wrapper">
                        <input type="checkbox" id="remember" name="remember" />
                        <span className="checkbox-label">
                            <span className="checkmark"></span>
                            Remember me
                        </span>
                    </label>
                    <a href="#" className="forgot-password">Forgot password?</a>
                </div>

                <button type="submit" className="login-btn">
                    <span className="btn-text">Sign In</span>
                    <span className="btn-loader"></span>
                </button>
            </form>

            <div className="signup-link">
                <p>Don't have an account? <a href="/sign-up">Create one</a></p>
            </div>

            <div className="success-message" id="successMessage">
                <div className="success-icon">✓</div>
                <h3>Login Successful!</h3>
            </div>
        </div>
    </div>
    )
}