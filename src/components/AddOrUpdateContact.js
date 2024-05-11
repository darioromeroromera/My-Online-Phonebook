import { useState, useEffect } from "react";
import { Helmet } from "react-helmet";
import Header from "./Header";
import './css/AddOrUpdateContact.css';
import { useNavigate } from "react-router-dom";

const AddOrUpdateContact = ({isEdit}) => {
    const navigate = useNavigate();

    // State for form fields
    const [contactName, setContactName] = useState('');
    const [fullName, setFullName] = useState('');
    const [phoneNumber, setPhoneNumber] = useState('');
    const [contactImage, setContactImage] = useState(null);

    const [contactNameFlag, setContactNameFlag] = useState(false);

    const [fullNameFlag, setFullNameFlag] = useState(false);

    const [phoneNumberFlag, setPhoneNumberFlag] = useState(false);

    const [contactImageFlag, setContactImageFlag] = useState(false);

    const [isErrorVisible, setIsErrorVisible] = useState(false);

    const [apiError, setApiError] = useState('');

    // State for form validation errors
    const [errors, setErrors] = useState({
        contactName: '',
        fullName: '',
        phoneNumber: '',
        contactImage: ''
    });

    const checkContactName = () => {
        setErrors(errors => {
            return contactName.trim() == '' ?  { ...errors, contactName: 'El campo nombre de contacto no puede estar vacío' } : { ...errors, contactName: '' };
        });
    }

    const checkFullName = () => {
        setErrors(errors => {
            return fullName.trim() == '' ?  { ...errors, fullName: 'El campo nombre completo no puede estar vacío' } : { ...errors, fullName: '' };
        });
    }

    const checkPhoneNumber = () => {
        setErrors(errors => {
            return phoneNumber.trim() == '' ?  { ...errors, phoneNumber: 'El campo telefono no puede estar vacío' } : /^([679])\d{8}$/.test(phoneNumber) ? { ...errors, phoneNumber: '' } : { ...errors, phoneNumber: 'Formato de teléfono incorrecto'};
        });
    }

    const checkContactImage = () => {
        if (contactImage != null) {
            setErrors(errors => {
                return (contactImage.type != 'image/jpeg' && contactImage.type != 'image/png') ? { ...errors, contactImage: 'El campo de imagen debe ser formato jpeg o png' } : { ...errors, contactImage: '' };
            });
        }
    }

    useEffect(() => {
        contactNameFlag ? checkContactName() : setContactNameFlag(true);
    }, [contactName]);

    useEffect(() => {
        fullNameFlag ? checkFullName() : setFullNameFlag(true);
    }, [fullName]);

    useEffect(() => {
        phoneNumberFlag ? checkPhoneNumber() : setPhoneNumberFlag(true);
    }, [phoneNumber]);

    useEffect(() => {
        contactImageFlag ? checkContactImage() : setContactImageFlag(true);
    }, [contactImage]);

    const addContact = () => {
        alert('ADD');
    }

    const updateContact = () => {
        alert('EDIT');
    }

    // Function to handle form submission
    const handleSubmit = (e) => {
        e.preventDefault();
        checkContactName();
        checkFullName();
        checkPhoneNumber();
        checkContactImage();

        if (errors.contactName == '' && errors.fullName == '' && errors.phoneNumber == '' && errors.contactImage == '')
            if (isEdit) {
                updateContact();
            } else {
                addContact();
            }


        
            // Perform submit action (POST or PUT)
            // Example:
            // fetch('http://localhost:8080/api/contacts', {
            //     method: 'POST',
            //     headers: {
            //         'Content-Type': 'application/json',
            //         'Authorization': `Bearer ${localStorage.getItem('token')}`
            //     },
            //     body: JSON.stringify({ contactName, fullName, phoneNumber, contactImage })
            // })
            // .then(response => {
            //     if (!response.ok) {
            //         throw new Error('Error al agregar o actualizar el contacto');
            //     }
            //     return response.json();
            // })
            // .then(data => {
            //     // Handle success
            //     console.log('Contact added/updated successfully:', data);
            //     navigate('/'); // Redirect to home page
            // })
            // .catch(error => {
            //     // Handle error
            //     console.error('Error:', error);
            // });
        
    };

    return (
        <div className="AddOrUpdateContact__Container">
            <Helmet>
                <title>{isEdit ? 'Actualizar' : 'Agregar'} Contacto - My Online Phonebook</title>
            </Helmet>

            <Header />

            <div className="AddOrUpdateContact__FormContainer">
                <form className="AddOrUpdateContact__Form" onSubmit={handleSubmit}>
                    <p>{isEdit ? 'Actualizar' : 'Agregar'} Contacto</p>
                    <input className="AddOrUpdateContact__Form__Input"
                        type="text"
                        placeholder="Nombre de Contacto"
                        value={contactName}
                        onChange={(e) => setContactName(e.target.value)}
                        required
                    />
                    <p className="AddOrUpdateContact__Error">{errors.contactName}</p>

                    <input className="AddOrUpdateContact__Form__Input"
                        type="text"
                        placeholder="Nombre Completo"
                        value={fullName}
                        onChange={(e) => setFullName(e.target.value)}
                        required
                    />
                    <p className="AddOrUpdateContact__Error">{errors.fullName}</p>

                    <input className="AddOrUpdateContact__Form__Input"
                        type="tel"
                        placeholder="Teléfono (ej. 612345678)"
                        value={phoneNumber}
                        onChange={(e) => setPhoneNumber(e.target.value)}
                        required
                    />
                    <p className="AddOrUpdateContact__Error">{errors.phoneNumber}</p>

                    <input className="AddOrUpdateContact__Form__Input"
                        type="file"
                        onChange={(e) => setContactImage(e.target.files[0])}
                    />

                    <p className="AddOrUpdateContact__Error">{errors.contactImage}</p>

                    <button className="AddOrUpdateContact__Form__Button" type="submit">{isEdit ? 'Guardar cambios' : 'Guardar'}</button>

                    <button className="AddOrUpdateContact__Form__Button__Back" onClick={e => {
                        e.preventDefault();
                        navigate('/');
                    }}>Volver a inicio</button>
                </form>
            </div>
        </div>
    );
};

export default AddOrUpdateContact;