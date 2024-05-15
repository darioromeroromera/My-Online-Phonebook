import { useState, useEffect } from "react";
import { Helmet } from "react-helmet";
import Header from "./Header";
import './css/AddOrUpdateContact.css';
import { useNavigate, useParams } from "react-router-dom";

const AddOrUpdateContact = ({isEdit}) => {
    const navigate = useNavigate();

    const [loading, setLoading] = useState(false);

    const [contactName, setContactName] = useState('');

    const [fullName, setFullName] = useState('');

    const [phoneNumber, setPhoneNumber] = useState('');

    const [contactImage, setContactImage] = useState(null);

    const [contactRender, setContactRender] = useState("");

    const [contactNameFlag, setContactNameFlag] = useState(false);

    const [fullNameFlag, setFullNameFlag] = useState(false);

    const [phoneNumberFlag, setPhoneNumberFlag] = useState(false);

    const [contactImageFlag, setContactImageFlag] = useState(false);

    const [isErrorVisible, setIsErrorVisible] = useState(false);

    const [apiError, setApiError] = useState('');

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
            const isValidFormat = contactImage.type === 'image/jpeg' || contactImage.type === 'image/png';

            if (isValidFormat) {
                setContactRender(URL.createObjectURL(contactImage));
            }
            setErrors(errors => ({
                ...errors,
                contactImage: isValidFormat ? '' : 'El campo de imagen debe ser formato jpeg o png'
            }));        
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

    const addContact = async () => {
        if (loading)
            return;
        setLoading(true);
        let imageData = null;
        if (contactImage) {
            imageData = await readFileAsBase64(contactImage);
            console.log(imageData);
        }
        const data = await fetch('http://localhost:8080/api/contacts', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                token: localStorage.getItem('token')
            },
            mode: 'cors',
            body: JSON.stringify({'contact_name': contactName, 'full_name': fullName, 'telefono': phoneNumber, 'contact_picture': imageData})
        });

        const json = await data.json();

        console.log(json);

        if (json.result === undefined) {
            setLoading(false);
            setApiError('Ha ocurrido un error desconocido. Inténtelo más tarde');
            setIsErrorVisible(true);
        } else if (json.result === 'error') {
            setLoading(false);
            setApiError(json.details);
            setIsErrorVisible(true);
        } else {
            setTimeout(() => {
                setLoading(false);
                navigate('/');
            }, 1000);
        }
        
    }

    const updateContact = async () => {
        if (loading)
            return;
        setLoading(true);
        let imageData = null;
        if (contactImage) {
            imageData = await readFileAsBase64(contactImage);
            console.log(imageData);
        }
        const data = await fetch('http://localhost:8080/api/contacts/' + id, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                token: localStorage.getItem('token')
            },
            mode: 'cors',
            body: JSON.stringify({'contact_name': contactName, 'full_name': fullName, 'telefono': phoneNumber, 'contact_picture': imageData})
        });

        const json = await data.json();

        console.log(json);

        if (json.result === undefined) {
            setLoading(false);
            setApiError('Ha ocurrido un error desconocido. Inténtelo más tarde');
            setIsErrorVisible(true);
        } else if (json.result === 'error') {
            setLoading(false);
            setApiError(json.details);
            setIsErrorVisible(true);
        } else {
            setTimeout(() => {
                setLoading(false);
                navigate('/');
            }, 1000);
        }
    }

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
    };

    const {id} = useParams();

    const loadContactData = async () => {
        const data = await fetch('http://localhost:8080/api/contacts/' + id, {
            headers: {
                token: localStorage.getItem('token')
            }
        });

        const json = await data.json();

        if (json.result === undefined) {
            setApiError('Ha ocurrido un error desconocido. Inténtelo más tarde');
            setIsErrorVisible(true);
        } else if (json.result === 'error') {
            setApiError(json.details);
            setIsErrorVisible(true);
        } else {
            setContactName(json.data.contact_name);
            setFullName(json.data.full_name);
            setPhoneNumber(json.data.telefono);
            if (json.data.contact_picture != null)
                setContactRender(json.data.contact_picture);
            console.log(json.data);
        }
    };

    useEffect(() => {
        if (isEdit)
            loadContactData();
    }, []);

    const readFileAsBase64 = (file) => {
        return new Promise((resolve, reject) => {
            const reader = new FileReader();
    
            reader.onload = () => {
                resolve(`data:${file.type};base64,${reader.result.split(',')[1]}`);
            };
    
            reader.onerror = () => {
                reject(reader.error);
            };

            reader.readAsDataURL(file);
        });
    };

    return (
        <div className="AddOrUpdateContact__Container">
            <Helmet>
                <title>{isEdit ? 'Actualizar' : 'Agregar'} Contacto - My Online Phonebook</title>
            </Helmet>

            <Header />


            {loading && 
                <div className='AddOrUpdateContact__SpinnerDiv'>
                    <div className='AddOrUpdateContact__Spinner'></div>    
                </div>}

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

                    <div className="AddOrUpdateContact__Form__FileInputContainer AddOrUpdateContact__Form__InputButton" >
                        <span for="contactPictureInput">Cambiar foto de contacto</span>
                        <input id="contactPictureInput" className="AddOrUpdateContact__Form__FileInput" type="file" onChange={(e) => setContactImage(e.target.files[0])} />
                    </div>

                    <p className="AddOrUpdateContact__Error">{errors.contactImage}</p>

                    <img className={contactRender == '' ? 'AddOrUpdateContact__Hidden' : 'AddOrUpdateContact__Form__Render'} src={contactRender}/>

                    <div className={isErrorVisible ? 'AddOrUpdateContact__ApiError' : 'AddOrUpdateContact__Hidden'}>
                        <p>{apiError}</p>
                    </div>

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