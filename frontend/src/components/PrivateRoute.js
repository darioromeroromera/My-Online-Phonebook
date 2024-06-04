import { useNavigate } from "react-router-dom";
import {useEffect, useState} from 'react';

const PrivateRoute = ({children}) => {
    const navigate = useNavigate();

    const [isLoggedIn, setIsLoggedIn] = useState(false);

    useEffect(() => {
        if (localStorage.getItem('token') == undefined) {
            setIsLoggedIn(false);
            navigate('/login');
        }
        else 
            setIsLoggedIn(true);
        
    }, [navigate]);

    if (isLoggedIn)
        return children;
};

export default PrivateRoute;