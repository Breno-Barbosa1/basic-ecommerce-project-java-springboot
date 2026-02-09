import './SignUp.css';

import { useNavigate } from 'react-router-dom';
import { useState} from 'react';

export default function SignUp() {
    const API_URL = import.meta.env.VITE_API_URL || "http://localhost:8080";

    const navigate = useNavigate();
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [address, setAddress] = useState('');

    const handleSignUp = async (e: any) => {
        e.preventDefault();

        try {
            const response = await fetch(`${API_URL}/auth/register`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    email,
                    password,
                    address
                })
            });

            if(response.ok) {
                alert("Account created Successfully!");
                navigate("/login");
            } else if (response.status === 409) {
                alert("Email already registered!");
            } else {
                alert("User Creation failed! Please try again.")
            }
        } catch (error) {
            console.error("error during sign up!", error);
            alert("Error during user creation. Please check your information and try again!");
        }
    }

        return (
            <div className="signup-container">
            <div className="signup-card">
                <div className="signup-header">
                    <h2>Sign Up</h2>
                    <p>Create a new account</p>
                </div>

                <form className="signup-form" id="signupForm" onSubmit={handleSignUp}>
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

                    <div className="form-group">
                        <div className="input-wrapper">
                            <input type="text" id="address" name="address" onChange={(e) => setAddress(e.target.value)} />
                            <label htmlFor="address">Address</label>
                        </div>
                    </div>

                    <button type="submit" className="signup-btn">
                        <span className="btn-text">Sign Up</span>
                        <span className="btn-loader"></span>
                    </button>
                </form>

                <div className="signup-link">
                    <p>Already have an account? <a href="/login">Login</a></p>
                </div>

                <div className="success-message" id="successMessage">
                    <div className="success-icon">✓</div>
                    <h3>Account created successfully!</h3>
                </div>
            </div>
        </div>
        )
};