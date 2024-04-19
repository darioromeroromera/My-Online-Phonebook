import { Helmet } from 'react-helmet';
import './css/NotFound.css';

const NotFound = () => {
    return (
        <div className="NotFound__container">

          <Helmet>
            <title>Página no encontrada - My Online Phonebook</title>
          </Helmet>

          <h1 className="NotFound__title">404 - Página no encontrada</h1>
          <p className="NotFound__message">La página solicitada no ha sido encontrada.</p>
          <img className="NotFound__image" src="sad-face.png" alt="404 Error" />
        </div>
      );
};

export default NotFound;