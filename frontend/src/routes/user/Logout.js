import { useContext,  useEffect } from 'react';
import { UserContext } from '../../userContext';
import { Navigate } from 'react-router-dom';

function Logout() {
    const userContext = useContext(UserContext);

    useEffect(function() {
        userContext.setUserContext(null)
    }, []);

    return (
        <Navigate replace to="/" />
    );

}

export default Logout