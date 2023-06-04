import { useState } from 'react';
import { Navigate } from 'react-router-dom';
import axios from 'axios'


function Register() {
    const [username, setUsername] = useState([]);
    const [password, setPassword] = useState([]);
    const [repeat_password, setRepPassword] = useState([]);
    const [email, setEmail] = useState([]);
    const [error, setError] = useState([]);

    async function handleRegister(e){
        e.preventDefault();
        
        try {
            const response = await axios.post("http://localhost:8000/register/", {
                username: username,
                email: email,
                password: password,
                repeat_password: repeat_password
            }, {
                withCredentials: false,
                headers: { 'Content-Type': 'application/json' }
            });
        
            const data = await response.data;
            const user = JSON.parse(data.user);
            if (user._id !== undefined) {
                window.location.href="/";
            } 
            else {
                setUsername("");
                setPassword("");
                setEmail("");
                setRepPassword("");
                setError(JSON.parse(data.message));
            }
        } catch (error) {
            console.error(error);
        }
    }

    return(
        <form onSubmit={handleRegister}>
            <input type="text" name="username" placeholder="Username" value={username} onChange={(e)=>(setUsername(e.target.value))}/>
            <input type="text" name="email" placeholder="Email" value={email} onChange={(e)=>(setEmail(e.target.value))} />
            <input type="password" name="password" placeholder="Password" value={password} onChange={(e)=>(setPassword(e.target.value))} />
            <input type="password" name="repeat_password" placeholder="Repeat password" value={repeat_password} onChange={(e)=>(setRepPassword(e.target.value))} />
            <input type="submit" name="submit" value="Register user" />
            <label>{error}</label>
        </form>
    );
}

export default Register;