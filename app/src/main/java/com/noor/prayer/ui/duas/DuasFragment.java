package com.noor.prayer.ui.duas;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.noor.prayer.R;
import com.noor.prayer.model.Dua;
import java.util.ArrayList;
import java.util.List;

public class DuasFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_duas, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        RecyclerView recyclerView = view.findViewById(R.id.recycler_duas);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        
        List<Dua> duas = new ArrayList<>();
        duas.add(new Dua("Upon Waking Up", "الْحَمْدُ لِلَّهِ الَّذِي أَحْيَانَا بَعْدَ مَا أَمَاتَنَا وَإِلَيْهِ النُّشُورُ", "All praise is for Allah who gave us life after having taken it from us and unto Him is the resurrection.", "Bukhari"));
        duas.add(new Dua("Before Sleeping", "بِاسْمِكَ اللَّهُمَّ أَمُوتُ وَأَحْيَا", "In Your name, O Allah, I die and I live.", "Muslim"));
        duas.add(new Dua("Leaving Home", "بِسْمِ اللهِ ، تَوَكَّلْتُ عَلَى اللهِ وَلَا حَوْلَ وَلَا قُوَّةَ إِلَّا بِاللهِ", "In the name of Allah, I place my trust in Allah, and there is no might nor power except with Allah.", "Abu Dawud"));
        duas.add(new Dua("Entering Mosque", "اللَّهُمَّ افْتَحْ لِي أَبْوَابَ رَحْمَتِكَ", "O Allah, open the gates of Your mercy for me.", "Muslim"));
        duas.add(new Dua("Leaving Mosque", "اللَّهُمَّ إِنِّي أَسْأَلُكَ مِنْ فَضْلِكَ", "O Allah, I ask You from Your bounty.", "Muslim"));
        
        DuasAdapter adapter = new DuasAdapter(duas);
        recyclerView.setAdapter(adapter);
    }
}
