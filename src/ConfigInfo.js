import { useState } from "react";
import { useNavigate } from "react-router-dom";

const ConfigInfo = () => {
    const[isPending,setIsPending] = useState(false);
    const[dc,setDc] = useState('dc1');
    const[fromDateTime,setFromDateTime] = useState(undefined); //check what can be put instead of undefined
    const[toDateTime,setToDateTime] = useState(undefined); //check what can be put instead of undefined
    const navigate = useNavigate();

    const handleSearch = (e) =>{
        e.preventDefault(); 
        const data = {dc,fromDateTime,toDateTime};
        // setIsPending(true);
        // fetch('http://localhost:8080/configs',{
        //     method:"POST",
        //     headers:{ "Content-type" : "application/json" },
        //     body: JSON.stringify(data)
        // }).then(()=> {
        //     console.log('new blog added')
        //     setIsPending(false);
        console.log("Submitted");
        navigate("/configs", { replace: true });
    };

    return ( 
        <div className="config-info">
            <h2>Config Bucket Changes</h2>
            <form onSubmit={handleSearch}>
                
                <label>Datacentre</label>
                <select
                value={dc}
                onChange={(e)=> setDc(e.target.value)}>
                    <option value="dc1">dc1</option>
                    <option value="dc2">dc2</option>
                    <option value="dc3">dc3</option>
                </select>

                <label>From Date and time</label>
                <input 
                    type="datetime-local" 
                    value = {fromDateTime}
                    onChange={(e) => setFromDateTime(e.target.value)}
                />
                <label>To Date and time</label>
                <input 
                    type="datetime-local" 
                    value = {toDateTime}
                    onChange={(e) => setToDateTime(e.target.value)}
                />

                {!isPending && <button>Search</button>}
                {isPending && <button disabled>Loading data...</button>}
                
               
            </form>
        </div>
     );
}
 
export default ConfigInfo;