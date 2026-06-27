package com.gpproject.adhera.doctor.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gpproject.adhera.doctor.data.DoctorViewModel

@Composable
fun SavePatientResultScreen(
    doctorViewModel: DoctorViewModel,
    testType:String,
    testResult:String,
    onDone:()->Unit
){

    val patients by doctorViewModel
        .patients
        .collectAsStateWithLifecycle()

    var patientName by remember{
        mutableStateOf("")
    }

    Column(
        modifier=Modifier
            .fillMaxSize()
            .padding(20.dp)
    ){

        Text(
            text="Save Test Result",
            style=MaterialTheme.typography.headlineSmall
        )

        Spacer(Modifier.height(20.dp))

        OutlinedTextField(
            value=patientName,
            onValueChange={patientName=it},
            modifier=Modifier.fillMaxWidth(),
            label={
                Text("Patient name")
            }
        )

        Spacer(Modifier.height(20.dp))

        Text("Existing Patients")

        Spacer(Modifier.height(10.dp))

        LazyColumn{

            items(patients){ patient->

                Card(
                    modifier=Modifier
                        .fillMaxWidth()
                        .padding(vertical=6.dp),

                    onClick={

                        doctorViewModel
                            .saveResultForExistingPatient(
                                patient.patientId,
                                testType,
                                testResult
                            )

                        onDone()
                    }

                ){

                    Text(
                        patient.patientName,
                        modifier=Modifier.padding(16.dp)
                    )

                }
            }

        }

        Spacer(Modifier.height(20.dp))

        Button(
            onClick={

                doctorViewModel
                    .createPatientAndSaveResult(
                        patientName,
                        testType,
                        testResult
                    )

                onDone()

            },
            modifier=Modifier.fillMaxWidth()
        ){

            Text("Create New Patient")

        }

    }
}