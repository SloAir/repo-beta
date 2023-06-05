import { useContext,  useEffect } from 'react';
import { UserContext } from '../userContext';
import { Navigate } from 'react-router-dom';
import axios from 'axios'

function Logout() {
    const userContext = useContext(UserContext); 
    useEffect(function(){
        const logout = async function(){
            userContext.setUserContext(null);
            try {
                const response = await axios.post("http://localhost:8000/logout/", {
                    withCredentials: false,
                    headers: { 'Content-Type': 'application/json' },
                });
                console.log(response);
            }
            catch (error) {
                console.error(error);
            }
        }
        logout();
    }, []);

    return (
        <Navigate replace to="/" />
    );

}

export default Logout