import { useNavigate } from "react-router-dom";

const PrivateRoute = ({children}) => {
    const navigate = useNavigate();

    if (localStorage.getItem('token') == undefined)
        navigate('/register');
    else
        return children;
};

export default PrivateRoute;