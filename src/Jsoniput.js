import React, { useState } from "react";
import { useNavigate } from "react-router-dom";

const ClientRegisterForm = () => {
  const [jsonInput, setJsonInput] = useState("");
  const [errorMessage, setErrorMessage] = useState("");
  const [successMessage, setSuccessMessage] = useState("");
  const navigate = useNavigate();

    //const[isPending,setIsPending] = useState(false);
    const handleSubmit = (e) =>{
        e.preventDefault();
        try {
            // Validate JSON input
            const jsonData = JSON.parse(jsonInput);
            fetch('http://localhost:8080/client',{
              method:"POST",
              headers:{ "Content-type" : "application/json"},
              body: JSON.stringify(jsonData)
          }).then(()=> {
              console.log("successful");
              navigate("/client", { replace: true });
          });
          } catch (error) {
            setErrorMessage("Invalid JSON format.");
            setSuccessMessage("");
          }
    };

  return (
    <div className="json-input">
      <h2>Client JSON</h2>
      <form onSubmit={handleSubmit}>
        <textarea
          value={jsonInput}
          onChange={(e) => setJsonInput(e.target.value)}
          placeholder="Enter JSON here"
          rows="10"
          cols="50"
        ></textarea>
        <br />
        <button type="submit">Register Client</button>
      </form>

      {errorMessage && <p style={{ color: "red" }}>{errorMessage}</p>}
      {successMessage && <p style={{ color: "green" }}>{successMessage}</p>}
    </div>
  );
};

export default ClientRegisterForm;
