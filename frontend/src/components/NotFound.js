import { Helmet } from 'react-helmet';
import './css/NotFound.css';

const NotFound = () => {
    return (
        <div className="NotFound__Container">

          <Helmet>
            <title>Página no encontrada - My Online Phonebook</title>
          </Helmet>

          <h1 className="NotFound__Title">Error 404 - NOT FOUND</h1>
          <p className="NotFound__Message">La página solicitada no ha sido encontrada.</p>
          <img className="NotFound__Image" src="http://localhost:3000/sad-face.png" alt="404 Error" />
        </div>
      );
};

export default NotFound;